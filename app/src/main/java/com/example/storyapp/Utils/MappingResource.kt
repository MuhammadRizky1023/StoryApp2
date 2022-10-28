package com.example.storyapp.utils

import androidx.test.espresso.IdlingResource
import com.example.storyapp.UI.Activities.LocationActivity

class MappingResource:IdlingResource {

    private var idlingResource: IdlingResource.ResourceCallback?=null

    override fun getName(): String {
        return MappingResource::class.java.simpleName
    }

    override fun isIdleNow(): Boolean {
        val isMap = LocationActivity.LocationUser==0

        if(isMap){
            idlingResource?.onTransitionToIdle()
        }

        return isMap
    }

    override fun registerIdleTransitionCallback(transition: IdlingResource.ResourceCallback?) {
        this.idlingResource=transition
    }
}