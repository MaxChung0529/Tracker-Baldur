package com.example.csc306b_cw

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddLogPopUp.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShowDetailsPopUp(mainAct: MainActivity) : DialogFragment() {
    val mainActivity = mainAct

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val popUpView = inflater.inflate(R.layout.fragment_show_deails_pop_up, container, false)

        // Inflate the layout for this fragment
        return popUpView
    }
}