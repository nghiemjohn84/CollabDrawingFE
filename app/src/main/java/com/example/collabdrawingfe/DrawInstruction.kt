package com.example.collabdrawingfe

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class DrawInstruction {
    var command: String? = ""
    var x: Float? = 0.0F
    var y: Float? = 0.0F
    var colour: Int? = 0
    var strokeWidth: Int? = 0
    override fun toString(): String {
        return "$command + $x + $y + $colour + $strokeWidth"
    }
}