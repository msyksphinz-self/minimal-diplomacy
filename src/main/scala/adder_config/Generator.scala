package adder_config

import java.io.{File, FileWriter}

import chisel3.Driver
import freechips.rocketchip.util.ElaborationArtefacts
import freechips.rocketchip.diplomacy.ValName

object Generator {
  final def main(args: Array[String]) {
    implicit val valName = ValName("testHarness")

    Driver.emitVerilog(
      new TestHarness()((new SmallConfig).toInstance)
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
