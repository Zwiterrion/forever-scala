package context

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

object ExecutionContextImplicit {
  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(5))
}
