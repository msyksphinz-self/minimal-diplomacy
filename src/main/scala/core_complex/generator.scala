// See LICENSE.SiFive for license details.

package core_complex

import java.io.{File, FileWriter}

import chisel3._
import freechips.rocketchip.config.Parameters
import chisel3.Driver
import freechips.rocketchip.unittest.Generator.{generateAnno, generateArtefacts, generateFirrtl, generateTestSuiteMakefrags}
import freechips.rocketchip.util.ElaborationArtefacts;

object Generator {
    final def main(args: Array[String]) {
        val p = (new Default2Config).toInstance
        // val p = (new Default1Config).toInstance
        Driver.emitVerilog(
            new TestHarness()(p)
        )
        ElaborationArtefacts.files.foreach { case (extension, contents) =>
            val f = new File(".", "TestHarness." + extension)
            val fw = new FileWriter(f)
            fw.write(contents())
            fw.close
        }
        // generateArtefacts
    }
}

/*
object Generator2 extends freechips.rocketchip.util.GeneratorApp {
    generateFirrtl
    generateAnno
    generateTestSuiteMakefrags // TODO: Needed only for legacy make targets
    generateArtefacts
}
*/