package adder_example

import java.io.{File, FileWriter}

import chisel3.Driver
import core_complex.{CoreComplexTestHarness, Default1Config}
import freechips.rocketchip.util.ElaborationArtefacts
import freechips.rocketchip.config.{Config, Parameters}
import chisel3._
import chisel3.internal.sourceinfo.SourceInfo
import chisel3.stage.ChiselStage
import chisel3.util.random.FibonacciLFSR
import freechips.rocketchip.diplomacy.{LazyModule, LazyModuleImp, NexusNode, RenderedEdge, SimpleNodeImp, SinkNode, SourceNode, ValName}

object Generator {
  final def main(args: Array[String]) {
    implicit val valName = ValName("testHarness")
    Driver.emitVerilog(
      new TestHarness()(Parameters.empty)
    )
    ElaborationArtefacts.files.foreach { case (extension, contents) =>
      val f = new File(".", "TestHarness." + extension)
      val fw = new FileWriter(f)
      fw.write(contents())
      fw.close
    }
    /*
    val verilog = (new ChiselStage).emitVerilog(
      new AdderTestHarness()(Parameters.empty)
    )
    println(s"```verilog\n$verilog```")
     */
  }
}
