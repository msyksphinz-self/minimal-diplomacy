# minimal-diplomacy

Chisel's Diplomacy minial example based on https://github.com/chipsalliance/rocket-chip/blob/master/docs/src/diplomacy/select_tutorial.md


## How to generated

```sh
make adder_example
```

### Generate Default Verilog Hardware

By default 2-operand, 8-bit adder test modules are generated:

- `src/main/scala/adder_example/Top.scala`

```scala
class AdderTestHarness()(implicit p: Parameters) extends LazyModule {
  val numOperands = 2
  val bitWidth = 8
  // val numOperands = 4
```

- Generated `TestHarness.v`

```verilog
module Adder(
  input  [7:0] auto_in_1,
  input  [7:0] auto_in_0,
  output [7:0] auto_out
);
  assign auto_out = auto_in_0 + auto_in_1; // @[LazyModule.scala 173:49]
endmodule
```

### Generate Other configuration

If you change `AdderTestHarness` class variable like that:

```scala
class AdderTestHarness()(implicit p: Parameters) extends LazyModule {
  // val numOperands = 2
  // val bitWidth = 8
  val numOperands = 4
  val bitWidth = 16
```

4-operand, 16-bit adder test modules are generated.

```verilog
module Adder(
  input  [15:0] auto_in_3,
  input  [15:0] auto_in_2,
  input  [15:0] auto_in_1,
  input  [15:0] auto_in_0,
  output [15:0] auto_out
);
  wire [15:0] _T_6 = auto_in_0 + auto_in_1; // @[Adder.scala 30:51]
  wire [15:0] _T_8 = _T_6 + auto_in_2; // @[Adder.scala 30:51]
  assign auto_out = _T_8 + auto_in_3; // @[LazyModule.scala 173:49]
endmodule
```
