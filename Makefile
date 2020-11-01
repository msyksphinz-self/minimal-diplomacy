export TERM=xterm-color

SBT ?= java -Xmx$(JVM_MEMORY) -Xss8M -XX:MaxPermSize=256M -jar ./sbt-launch.jar
# SBT ?= java -Xmx$(JVM_MEMORY) -Xss8M -XX:MaxPermSize=256M -jar ./sbt/bin/sbt-launch.jar
# SBT ?= sbt -mem 2048

PROJ_ROOT = $(shell git rev-parse --show-toplevel)

generated_dir = $(abspath ./generated-src)
generated_dir_debug = $(abspath ./generated-src-debug)

# PROJECT ?= freechips.rocketchip.system
PROJECT ?= core_complex
CONFIG  ?= $(PROJECT).DefaultConfig

JAVA_HEAP_SIZE ?= 8G
JAVA_ARGS ?= -Xmx$(JAVA_HEAP_SIZE) -Xss8M -XX:MaxPermSize=256M

.PHONY = fir_build

export JAVA_ARGS

include Makefrag-verilator

core_complex: CoreComplexTestHarness.sv
	mkdir -p $(generated_dir_debug)/$(long_name)
	$(VERILATOR) $(VERILATOR_FLAGS) -Mdir $(generated_dir_debug)/$(long_name) \
	-o $(abspath $(sim_dir))/$@ $(verilog) $(cppfiles) -LDFLAGS "$(LDFLAGS)" \
	-CFLAGS "-I$(generated_dir_debug)"
	$(MAKE) VM_PARALLEL_BUILDS=1 -C $(generated_dir_debug)/$(long_name) -f V$(MODEL).mk
	./$@

CoreComplexTestHarness.sv:
	$(SBT) 'runMain $(PROJECT).Generator $(generated_dir_debug) core_complex CoreComplexTestHarness $(PROJECT)'

adder_example: AdderExampleTestHarness.sv
	mkdir -p $(generated_dir_debug)/$(long_name)
	$(VERILATOR) $(VERILATOR_FLAGS) -Mdir $(generated_dir_debug)/$(long_name) \
	-o $(abspath $(sim_dir))/$@ $(verilog) $(cppfiles) -LDFLAGS "$(LDFLAGS)" \
	-CFLAGS "-I$(generated_dir_debug)"
	$(MAKE) VM_PARALLEL_BUILDS=1 -C $(generated_dir_debug)/$(long_name) -f V$(MODEL).mk
	./$@

AdderExampleTestHarness.sv:
	$(SBT) 'runMain $(PROJECT).Generator $(generated_dir_debug) AdderExample AdderExampleTestHarness $(PROJECT)'


mkdir_generated_dir:
	mkdir -p $(generated_dir) $(generated_dir_debug)

clean:
	$(RM) -rf test_run_dir target *.v *.fir *.json *.log generated-src-debug *.sv
	$(RM) -rf *.d
	$(RM) -rf *.graphml
	$(RM) -rf *.plusArgs
