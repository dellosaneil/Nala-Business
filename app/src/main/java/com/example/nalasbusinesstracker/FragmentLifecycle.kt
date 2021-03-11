package com.example.nalasbusinesstracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

open class FragmentLifecycle : Fragment() {
    
    
    private val TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate: 1")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView: 2")
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated: 3")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.i(TAG, "onViewStateRestored: 4")
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart: 5")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume: 6")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause: 7")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop: 8")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSaveInstanceState: 9")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(TAG, "onDestroyView: 10")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: 11")
    }
    





}