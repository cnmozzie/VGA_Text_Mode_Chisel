/*
 * VGA DRIVE: Hello World of VGA Text Mode
 */

import chisel3._

/**
 * The VGA DRIVE component.
 */

class VGA_DRIVE extends Module {
  val io = IO(new Bundle {
    val vga_hs = Output(UInt(1.W))
    val vga_vs = Output(UInt(1.W))
    val vga_rgb = Output(UInt(12.W))
  })
  
  val pll_vga_inst = Module(new pll_vga)
  pll_vga_inst.io.reset := reset
  pll_vga_inst.io.clk_in1 := clock

  val blk_mem_inst = Module(new blk_mem)
  blk_mem_inst.io.clka := clock;
  blk_mem_inst.io.wea := false.B;
  blk_mem_inst.io.addra := 0.U;
  blk_mem_inst.io.dina := 0.U;
  blk_mem_inst.io.clkb := pll_vga_inst.io.clk_out1;
  

  val vga_ctrl = withClockAndReset(pll_vga_inst.io.clk_out1, !pll_vga_inst.io.locked) {Module(new VGA_CTRL)} 
  blk_mem_inst.io.addrb := vga_ctrl.io.addr;
  vga_ctrl.io.data := blk_mem_inst.io.doutb;
  io.vga_hs := vga_ctrl.io.vga_hs
  io.vga_vs := vga_ctrl.io.vga_vs
  io.vga_rgb := vga_ctrl.io.vga_rgb
  

}

class pll_vga extends BlackBox {
  val io = IO(new Bundle {
    val reset = Input(Reset())
    val clk_in1 = Input(Clock())
    val clk_out1 = Output(Clock())
    val locked = Output(Bool())
  })
}

class blk_mem extends BlackBox {
  val io = IO(new Bundle {
    val clka = Input(Clock())
    val wea = Input(Bool())
    val addra = Input(UInt(8.W))
    val dina = Input(UInt(16.W))
    val clkb = Input(Clock())
    val addrb = Input(UInt(12.W))
    val doutb = Output(UInt(16.W))
  })
}

/**
 * An object extending App to generate the Verilog code.
 */
object VGA_DRIVE extends App {
  (new chisel3.stage.ChiselStage).emitVerilog(new VGA_DRIVE())
}
