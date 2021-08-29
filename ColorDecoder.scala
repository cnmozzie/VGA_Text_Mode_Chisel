/*
 * This code is a minimal hardware described in Chisel.
 * 
 * Blinking LED: the FPGA version of Hello World
 */

import chisel3._
import chisel3.util._

/**
 * The blinking LED component.
 */

class ColorDecoder extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(4.W))
    val out = Output(UInt(12.W))
  })

  val color = RegInit(0.U(12.W))
  color := 0.U

  switch(io.in) {
    is ("h0".U) { color := "h000".U} // Black
    is ("h1".U) { color := "h00F".U} // Blue
    is ("h2".U) { color := "h0F0".U} // Green
    is ("h3".U) { color := "h0FF".U} // Cyan
    is ("h4".U) { color := "hF00".U} // Red
    is ("h5".U) { color := "hF0F".U} // Magenta
    is ("h6".U) { color := "hA22".U} // Brown
    is ("h7".U) { color := "hDDD".U} // Light Gray
    is ("h8".U) { color := "hAAA".U} // Dark Gray
    is ("h9".U) { color := "hADE".U} // Light Blue
    is ("ha".U) { color := "h9E9".U} // Light Green
    is ("hb".U) { color := "hEFF".U} // Light Cyan
    is ("hc".U) { color := "hF88".U} // Light Red
    is ("hd".U) { color := "hFCC".U} // Pink
    is ("he".U) { color := "hFF0".U} // Yellow
    is ("hf".U) { color := "hFFF".U} // White
  }

  io.out := RegNext(color)
}

