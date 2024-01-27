import { useEffect, useRef, useState } from 'react';
import { useLocation } from 'react-router-dom';
import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs';

function ChatRoom(){
	useEffect(()=>{
		connectChat();
	}, [])
	let roomId = useLocation().state
	
  const [message, setMessage] = useState('');
	
  // stomp 세션 연결 유지를 위한 변수
  const client = useRef();

  const onMessageChange = (e)=>{
    setMessage(e.target.value)
  }
	
	const connectChat = ()=>{
    const socket = new SockJS('http://localhost:8080/ws-stomp')
    client.current = Stomp.over(socket)
    client.current.connect({}, onConnected, onError); 
  }
        
  function onConnected(){
      client.current.subscribe('/sub/chat/room/' + roomId, onMessageReceived);
      client.current.send("/pub/chat/enterUser", {}, JSON.stringify({type: 'ENTER', "roomId": roomId, sender: "kim",}))
    }

  function onError(error) {
      alert('error')
  }


  function onMessageReceived(payload) {
      const chattingWindow = document.querySelector('.chat')
      var chat = JSON.parse(payload.body);
      console.log("전송받은 데이터: ", chat.message)
      var value = chattingWindow.value + '\n' + chat.message
      chattingWindow.value = value;
  }

  function sendMessage(){
      client.current.send("/pub/chat/sendMessage", {}, JSON.stringify({
          "roomId": roomId,
          sender: 'kim',
          message: message,
          type: 'TALK'
      }))
      setMessage('')
  }


	return(
		<>
			<textarea className='chat' ></textarea>
			<input onChange={onMessageChange}/>
			<button onClick={sendMessage}>메시지 전송</button>
		</>
	);
};

export default ChatRoom;