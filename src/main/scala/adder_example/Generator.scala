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
import freechips.rocketchip.diplomacy.{SimpleNodeImp, RenderedEdge, ValName, SourceNode,
  NexusNode, SinkNode, LazyModule, LazyModuleImp}

object Generator {
  final def main(args: Array[String]) {
    Driver.emitVerilog(
      LazyModule(new AdderTestHarness()(Parameters.empty)).module
    )
    /*
    val verilog = (new ChiselStage).emitVerilog(
      new AdderTestHarness()(Parameters.empty)
    )
    println(s"```verilog\n$verilog```")
     */
  }
}
