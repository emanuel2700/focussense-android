import pe.edu.uni.focussense.adaptation.AdaptationEngine
import pe.edu.uni.focussense.model.AdaptationMode
import pe.edu.uni.focussense.model.ContextSnapshot

fun main() {
    val engine = AdaptationEngine()

    check(engine.decide(ContextSnapshot(300f, 80, false)).mode == AdaptationMode.NORMAL)
    check(engine.decide(ContextSnapshot(10f, 80, false)).mode == AdaptationMode.DARK)
    check(engine.decide(ContextSnapshot(1200f, 80, false)).mode == AdaptationMode.OUTDOOR)
    check(engine.decide(ContextSnapshot(300f, 15, false)).mode == AdaptationMode.ECO)
    check(engine.decide(ContextSnapshot(10f, 15, false)).mode == AdaptationMode.DARK_ECO)
    check(engine.decide(ContextSnapshot(1200f, 15, false)).mode == AdaptationMode.OUTDOOR_ECO)

    println("OK: 6 escenarios del motor adaptativo validados.")
}
