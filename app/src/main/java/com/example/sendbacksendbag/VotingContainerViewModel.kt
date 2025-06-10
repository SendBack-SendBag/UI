package com.example.sendbacksendbag

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.sendbacksendbag.ui.voting.Poll
import com.example.sendbacksendbag.ui.voting.myPoll

class VotingContainerViewModel : ViewModel() {
    // 전체 투표 목록
    private val _polls = mutableStateListOf<Poll>()
    val polls: List<Poll> = _polls

    // 내 투표 목록
    private val _myPolls = mutableStateListOf<myPoll>()
    val myPolls: List<myPoll> = _myPolls


    // 새 투표 추가 메소드
    fun addPoll(poll: Poll) {
        _polls.add(poll)
    }

    // 내 투표 추가 메소드
    fun addMyPoll(poll: myPoll) {
        _myPolls.add(poll)
    }
}