import { useEffect, useRef, useState } from 'react';
import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs';
import axios from 'axios';

function Main(){
    useEffect(()=>{

        axios.get(`http://${SERVER_URL}/rooms/timer`)
        .then(res=>console.log(res))
    // 방이 실시간으로 바뀌는 것을 인지하기 위해 항상 해당 채널을 구독해야한다.
    connectSession();
    }, [])

    const SERVER_URL = process.env.REACT_APP_API_URL
    const client = useRef();
    // 세션 연결
    const connectSession = ()=>{
        const socket = new SockJS(`http://${SERVER_URL}/ws-stomp`)
        client.current = Stomp.over(socket)
        client.current.connect({}, onConnected, onError); 
    }
        
  // 세션이 연결되는 순간 방 리스트를 출력하는 토픽과 전체 채팅 관련 토픽을 구독해서 메시지를 기다린다.
  function onConnected(){
    client.current.subscribe(`/sub/timer`, onTimerReceived);
  }

  function onTimerReceived(payload){
    console.log(JSON.parse(payload.body))
  }

  // 에러 처리 콜백 함수
  function onError(error) {
    alert('error')
  }
    return(
    <div>
        <h1>메인 입니다</h1>
    </div>
    );
};

export default Main;