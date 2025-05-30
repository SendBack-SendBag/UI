package com.example.sendbacksendbag.ui.voting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sendbacksendbag.data.FriendsRepository

/**
 * VotingViewModel에 FriendsRepository를 주입하기 위한 Factory 클래스.
 */
class VotingViewModelFactory(private val repository: FriendsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 요청된 ViewModel 클래스가 VotingViewModel과 호환되는지 확인
        if (modelClass.isAssignableFrom(VotingViewModel::class.java)) {
            // 호환된다면 FriendsRepository를 전달하여 VotingViewModel 인스턴스 생성
            @Suppress("UNCHECKED_CAST")
            return VotingViewModel(repository) as T
        }
        // 호환되지 않으면 예외 발생
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}