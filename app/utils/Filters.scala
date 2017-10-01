package utils

import javax.inject.Inject

import play.api.http.HttpFilters
import play.api.mvc.EssentialFilter
import play.filters.csrf.CSRFFilter
import play.filters.headers.SecurityHeadersFilter

class Filters @Inject()(csrfFilter: CSRFFilter, securityHeadersFilter: SecurityHeadersFilter)
    extends HttpFilters {

  override def filters: Seq[EssentialFilter] = Seq(securityHeadersFilter)
}
