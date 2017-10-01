package module

import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides, TypeLiteral}
import com.mohiva.play.silhouette.api.actions.{SecuredErrorHandler, UnsecuredErrorHandler}
import com.mohiva.play.silhouette.api.crypto.{CookieSigner, Crypter, CrypterAuthenticatorEncoder}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.crypto.{JcaCookieSigner, JcaCookieSignerSettings, JcaCrypter, JcaCrypterSettings}
import com.mohiva.play.silhouette.impl.authenticators._
import com.mohiva.play.silhouette.impl.util.{DefaultFingerprintGenerator, SecureRandomIDGenerator}
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import dao._
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import play.api.Configuration
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.WSClient
import service.{UserService, UserServiceImpl}
import utils.auth.{CustomSecuredErrorHandler, CustomUnsecuredErrorHandler, DefaultEnv}

class Module extends AbstractModule {

  override def configure() = {
    bind(new TypeLiteral[Silhouette[DefaultEnv]] {})
      .to(new TypeLiteral[SilhouetteProvider[DefaultEnv]] {})

    bind(classOf[UnsecuredErrorHandler]).to(classOf[CustomUnsecuredErrorHandler])
    bind(classOf[SecuredErrorHandler]).to(classOf[CustomSecuredErrorHandler])
    bind(classOf[UserDao]).to(classOf[UserDaoImpl])
    bind(classOf[UserService]).to(classOf[UserServiceImpl])
    bind(classOf[IDGenerator]).toInstance(new SecureRandomIDGenerator())
    bind(classOf[PasswordHasher]).toInstance(new BCryptPasswordHasher)
    bind(classOf[FingerprintGenerator]).toInstance(new DefaultFingerprintGenerator(false))
    bind(classOf[EventBus]).toInstance(EventBus())
    bind(classOf[Clock]).toInstance(Clock())

    bind(new TypeLiteral[DelegableAuthInfoDAO[PasswordInfo]] {}).to(classOf[PasswordInfoDAOImpl])
  }

  /**
    * Provides the HTTP layer implementation.
    *
    * @param client Play's WS client.
    * @return The HTTP layer implementation.
    */
  @Provides
  def provideHTTPLayer(client: WSClient): HTTPLayer = new PlayHTTPLayer(client)

  /**
    * Provides the Silhouette environment.
    *
    * @param userService          The user service implementation.
    * @param authenticatorService The authentication service implementation.
    * @param eventBus             The event bus instance.
    * @return The Silhouette environment.
    */
  @Provides
  def provideEnvironment(userService: UserService,
                         authenticatorService: AuthenticatorService[JWTAuthenticator],
                         eventBus: EventBus): Environment[DefaultEnv] =
  Environment[DefaultEnv](userService, authenticatorService, Seq(), eventBus)

  /**
    * Provides the cookie signer for the authenticator.
    *
    * @param configuration The Play configuration.
    * @return The cookie signer for the authenticator.
    */
  @Provides
  @Named("authenticator-cookie-signer")
  def provideAuthenticatorCookieSigner(configuration: Configuration): CookieSigner = {
    val config =
      configuration.underlying.as[JcaCookieSignerSettings]("silhouette.authenticator.cookie.signer")

    new JcaCookieSigner(config)
  }

  /**
    * Provides the crypter for the authenticator.
    *
    * @param configuration The Play configuration.
    * @return The crypter for the authenticator.
    */
  @Provides
  @Named("authenticator-crypter")
  def provideAuthenticatorCrypter(configuration: Configuration): Crypter = {
    val config = configuration.underlying.as[JcaCrypterSettings]("silhouette.authenticator.crypter")

    new JcaCrypter(config)
  }

  /**
    * Provides the authenticator service.
    *
    * @param cookieSigner         The cookie signer implementation.
    * @param crypter              The crypter implementation.
    * @param fingerprintGenerator The fingerprint generator implementation.
    * @param idGenerator          The ID generator implementation.
    * @param configuration        The Play configuration.
    * @param clock                The clock instance.
    * @return The authenticator service.
    */
  @Provides
  def provideAuthenticatorService(@Named("authenticator-crypter") crypter: Crypter,
                                  idGenerator: IDGenerator,
                                  configuration: Configuration,
                                  clock: Clock): AuthenticatorService[JWTAuthenticator] = {
    val settings = JWTAuthenticatorSettings(
      sharedSecret = configuration.getString("play.crypto.secret").get
    )
    val encoder = new CrypterAuthenticatorEncoder(crypter)

    new JWTAuthenticatorService(settings, None, encoder, idGenerator, clock)
  }

  /**
    * Provides the password hasher registry.
    *
    * @param passwordHasher The default password hasher implementation.
    * @return The password hasher registry.
    */
  @Provides
  def providePasswordHasherRegistry(passwordHasher: PasswordHasher): PasswordHasherRegistry =
  new PasswordHasherRegistry(passwordHasher)

  /**
    * Provides the auth info repository.
    *
    * @param passwordInfoDAO The implementation of the delegable password auth info DAO.
    * @return The auth info repository instance.
    */
  @Provides
  def provideAuthInfoRepository(passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo]
                               ): AuthInfoRepository =
  new DelegableAuthInfoRepository(passwordInfoDAO)

}
