package com.example.csc306b_cw

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Logs.newInstance] factory method to
 * create an instance of this fragment.
 */
class Logs : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val contentView = inflater.inflate(R.layout.fragment_logs, container,false)
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.recycler)

        val dataList = populateList()
        val adapter = ItemsAdapter(dataList)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this.activity)
        recyclerView.layoutManager = layoutManager

        return contentView
    }

    private fun populateList() : ArrayList<LogsData> {

        val list = ArrayList<LogsData>()

        val data = LogsData()
        data.activityName = "Reading"
        data.activityTime = "7pm - 8pm"
        list.add(data)


        return list
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Logs.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Logs().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}