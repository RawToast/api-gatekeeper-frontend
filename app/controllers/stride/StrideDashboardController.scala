/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.stride

import connectors.{ApplicationConnector, DeveloperConnector, StrideAuthConnector}
import controllers.{BaseController, WithAppConfig, routes}
import model.State.{State, _}
import model._
import org.joda.time.DateTime
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.mvc.{Action, AnyContent, Result}
import uk.gov.hmrc.auth.core.AuthProvider.PrivilegedApplication
import uk.gov.hmrc.auth.core.{AuthProviders, AuthorisedFunctions, Enrolment}
import views.html.dashboard._

import scala.concurrent.Future

object StrideDashboardController extends StrideDashboardController with WithAppConfig {
  override val applicationConnector = ApplicationConnector
  override val developerConnector = DeveloperConnector
  override def authConnector = StrideAuthConnector
}

trait StrideDashboardController extends BaseController with AuthorisedFunctions {

  val applicationConnector: ApplicationConnector
  val developerConnector: DeveloperConnector
  implicit val dateTimeOrdering: Ordering[DateTime] = Ordering.fromLessThan(_ isBefore _)


  def strideDashboardPage: Action[AnyContent] = Action.async {
    implicit request => {
      authorised(Enrolment("gatekeeper") and AuthProviders(PrivilegedApplication)) {
          dashboardView
      }
    }
  }

  private def dashboardView = {
    def applicationsForDashboard(apps: Seq[ApplicationWithUpliftRequest]) = {
      val grouped: Map[State, Seq[ApplicationWithUpliftRequest]] = apps.groupBy(_.state)
      val pendingApproval = grouped.getOrElse(PENDING_GATEKEEPER_APPROVAL, Seq())
      val pendingVerification = grouped.getOrElse(PENDING_REQUESTER_VERIFICATION, Seq()) ++ grouped.getOrElse(PRODUCTION, Seq())

      CategorisedApplications(pendingApproval.sortBy(_.submittedOn), pendingVerification.sortBy(_.name.toLowerCase))
    }

    for {
      apps <- applicationConnector.fetchApplicationsWithUpliftRequest()
      mappedApps = applicationsForDashboard(apps)
    } yield Ok(dashboard(mappedApps))
  }


  private def redirectIfExternalTestEnvironment(body: => Future[Result]) = {
    if (appConfig.isExternalTestEnvironment) Future.successful(Redirect(routes.ApplicationController.applicationsPage))
    else body
  }
}
