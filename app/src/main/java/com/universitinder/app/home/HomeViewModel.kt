package com.universitinder.app.home

//import android.util.Log
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universitinder.app.controllers.FilterController
import com.universitinder.app.controllers.SchoolController
import com.universitinder.app.helpers.ActivityStarterHelper
import com.universitinder.app.models.SchoolPlusImages
import com.universitinder.app.models.UserState
import com.universitinder.app.school.profile.SchoolProfileActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val schoolController: SchoolController,
    private val filterController: FilterController,
    private val activityStarterHelper: ActivityStarterHelper
): ViewModel() {
    private val currentUser = UserState.currentUser
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState : StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

//    private suspend fun getFilters() {
//        if (currentUser != null) {
//            viewModelScope.launch(Dispatchers.IO) {
//                val filter = filterController.getFilter(currentUser.email)
//                Log.w("HOME VIEW MODEL", filter.toString())
//                if (filter != null) _uiState.value = _uiState.value.copy(filter = filter)
//            }
//        }
//    }

    fun refresh() {
        if (currentUser != null) {
            viewModelScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Main) { _uiState.value = _uiState.value.copy(fetchingLoading = true) }
                val filter = filterController.getFilter(currentUser.email)
//                Log.w("HOME VIEW MODEL", filter.toString())
                if (filter != null) {
                    val schools = schoolController.getFilteredSchool(filter)
//                    Log.w("HOME VIEW MODEL", schools.toString())
                    withContext(Dispatchers.Main) {
                        _uiState.value = _uiState.value.copy(
                            fetchingLoading = false,
                            schools = schools
                        )
                    }
                }
            }
        }
    }

    fun onSwipeRight() {
        _uiState.value = _uiState.value.copy(currentIndex = _uiState.value.currentIndex+1)
    }

    fun onSwipeLeft() {
        _uiState.value = _uiState.value.copy(currentIndex = _uiState.value.currentIndex+1)
    }

    fun startSchoolProfileActivity(school: SchoolPlusImages) {
        val intent = Intent(activityStarterHelper.getContext(), SchoolProfileActivity::class.java)
        intent.putExtra("school", school)
        activityStarterHelper.startActivity(intent)
    }
}