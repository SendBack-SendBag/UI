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

    // 초기 데이터 로드
    init {
        // 샘플 데이터 추가
        _polls.add(Poll("1", "친구의 피드백에 대한 투표", "친구의 조언이 도움이 되었나요?"))
        _polls.add(Poll("2", "팀 프로젝트 진행 방향 투표", "다음 단계로 어떤 작업을 진행해야 할까요?"))
    }

    // 새 투표 추가 메소드
    fun addPoll(poll: Poll) {
        _polls.add(poll)
    }

    // 내 투표 추가 메소드
    fun addMyPoll(poll: myPoll) {
        _myPolls.add(poll)
    }
}