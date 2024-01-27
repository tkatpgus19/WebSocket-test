import { useEffect, useRef, useState } from 'react';
import { useLocation } from 'react-router-dom';
import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs';
import axios from 'axios';

function ChatRoom(){
	useEffect(()=>{
		connectChat();
	}, [])
	let location = useLocation()
	const {roomId, nickname} = location.state
  const [message, setMessage] = useState('');
	console.log('닉네임: '+nickname)
	
  // stomp 세션 연결 유지를 위한 변수
  const client = useRef();

  const onMessageChange = (e)=>{
    setMessage(e.target.value)
  }
	const onKeyDown = (e)=>{
    if(e.keyCode === 13){
			sendMessage()
      setMessage('')
    }
  }
	
	const connectChat = ()=>{
    const socket = new SockJS('http://localhost:8080/ws-stomp')
    client.current = Stomp.over(socket)
    client.current.connect({}, onConnected, onError); 
  }
        
  function onConnected(){
      client.current.subscribe('/sub/chat/room/' + roomId, onMessageReceived);
      client.current.send("/pub/chat/enterUser", {}, JSON.stringify({type: 'ENTER', "roomId": roomId, sender: nickname,}))
    }

  function onError(error) {
      alert('error')
  }


  function onMessageReceived(payload) {
      const chattingWindow = document.querySelector('.chat')
      var chat = JSON.parse(payload.body);
      console.log("전송받은 데이터: ", chat)
      var value = `${chattingWindow.value}\n[${chat.sender}]\n${chat.message}`
      chattingWindow.value = value;
  }

  function sendMessage(){
      client.current.send("/pub/chat/sendMessage", {}, JSON.stringify({
          "roomId": roomId,
          sender: nickname,
          message: message,
          type: 'TALK'
      }))
      setMessage('')
  }

	function showMembers(){
		console.log(roomId)
		axios.get('http://localhost:8080/chat/userlist', {'roomId': roomId})
      .then(res=>{
        console.log(res)
      })
      .catch(err=>console.log(err))
	}

	return(
		<>
			<div>
				<textarea className='chat' ></textarea>
			</div>
			<input onChange={onMessageChange} onKeyDown={onKeyDown} value={message}/>
			<button onClick={sendMessage}>메시지 전송</button>
			<button onClick={showMembers}>이 방의 맴버 정보 보기</button>
		</>
	);
};

export default ChatRoom;