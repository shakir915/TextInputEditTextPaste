  val vrfs = arrayOf(vrf1, vrf2, vrf3, vrf4, vrf5, vrf6)
            vrfs.forEachIndexed { index, textInputEditTextPaste ->
                textInputEditTextPaste.onPasteDone={
                    viewModel!!.fieldSubmitted.call()
                }
            }
            vrfs.forEachIndexed { index, textInputEditText ->
                textInputEditText.setOnKeyListener { v, keyCode, event ->

                    println("eventeventevent $keyCode ${event.action} $event")
                    viewModel!!.fieldSubmitted.call()
                    //println("textInputEditText $index keyCode $keyCode $event Signup2Fragment : onViewCreated() called with: v = $v, keyCode = $keyCode, event = $event")
                    if (keyCode in 7..17&&event.action==ACTION_UP) {
                        if (index == 5) {
                            textInputEditText.hideKeyboardView()
                        } else
                            vrfs[index + 1].requestFocus()
                        return@setOnKeyListener false
                    }else if (keyCode==67&&event.action==ACTION_UP){
                        textInputEditText.setText("")
                        vrfs.getOrNull(index - 1)?.requestFocus()
                    }


                    return@setOnKeyListener false
                }
            }
