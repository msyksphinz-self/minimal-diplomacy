package adder_example

import chipsalliance.rocketchip.config.{Config, Parameters}
import chisel3._
import chisel3.internal.sourceinfo.SourceInfo
import chisel3.stage.ChiselStage
import chisel3.util.random.FibonacciLFSR
import core_complex.core_complex
import freechips.rocketchip.diplomacy.{LazyModule, LazyModuleImp, NexusNode, RenderedEdge, SimpleNodeImp, SinkNode, SourceNode, ValName}
import freechips.rocketchip.util.ElaborationArtefacts

/** top-level connector */
class AdderTestHarness()(implicit p: Parameters) extends LazyModule {
  val numOperands = 2
  val bitWidth = 8
  // val numOperands = 4
  // val bitWidth = 16

  val adder = LazyModule(new Adder)
  // 8 will be the downward-traveling widths from our drivers
  val drivers = Seq.fill(numOperands) { LazyModule(new AdderDriver(width = bitWidth, numOutputs = 2)) }
  // 4 will be the upward-traveling width from our monitor
  val monitor = LazyModule(new AdderMonitor(width = bitWidth, numOperands = numOperands))

  // create edges via binding operators between nodes in order to define a complete graph
  drivers.foreach{ driver => adder.node := driver.node }

  drivers.zip(monitor.nodeSeq).foreach { case (driver, monitorNode) => monitorNode := driver.node }
  monitor.nodeSum := adder.node

  lazy val module = new LazyModuleImp(this) {
    val io = IO(new Bundle {
      val finished = Output(Bool())
    })
    when(monitor.module.io.error) {
      printf("something went wrong")
    }
    io.finished := monitor.module.io.error
  }

  override lazy val desiredName = "AdderTestHarness"
}

class TestHarness()(implicit p: Parameters) extends Module {
  val io = IO(new Bundle {
    val success = Output(Bool())
  })

  val ldut = LazyModule(new AdderTestHarness)
  val dut = Module(ldut.module)
  io.success := dut.io.finished

  ElaborationArtefacts.add("graphml", ldut.graphML)
}
