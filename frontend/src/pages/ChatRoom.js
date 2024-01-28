import { useEffect, useRef, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs';
import axios from 'axios';

function ChatRoom(){
	useEffect(()=>{
		console.log(roomId)
		connectChat();
		
	}, [])
	let location = useLocation()
	const navigate = useNavigate();
	const {roomId, nickname} = location.state
  const [message, setMessage] = useState('');
	const [userlist, setUserlist] = useState([]);
	
	
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
			showMembers()
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
		axios.get(`http://localhost:8080/chat/userlist/${roomId}`)
      .then(res=>{
        console.log(res.data)
				setUserlist(res.data)
      })
      .catch(err=>console.log(err))
	}

	return(
		<>
			<div>
				<textarea className='chat'></textarea>
				<textarea className='userlist' value={userlist}></textarea>
			</div>
			<input onChange={onMessageChange} onKeyDown={onKeyDown} value={message}/>
			<button onClick={sendMessage}>메시지 전송</button>
			<button onClick={()=>{
				client.current.disconnect();
				navigate(-1)
			}}>방 나가기</button>
		</>
	);
};

export default ChatRoom;