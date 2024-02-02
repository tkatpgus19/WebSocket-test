import { useEffect, useRef, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs';
import axios from 'axios';
import style from '../styles/WaitingRoom.module.css'

function ChatRoom(){
	useEffect(()=>{
		connectChat();
	}, [])
	let location = useLocation()
	const navigate = useNavigate();
	const {roomId, nickname, roomType} = location.state
  const [message, setMessage] = useState('');
	const [userlist, setUserlist] = useState([]);
  const [readylist, setReadylist] = useState([]);
  const [master, setMaster] = useState('');
	
	
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
    client.current.send("/pub/chat/enterUser", {}, JSON.stringify({type: 'ENTER', "roomId": roomId, sender: nickname, roomType: roomType}))
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
      chattingWindow.scrollTop = chattingWindow.scrollHeight
			getuserList()
  }

  function sendMessage(){
      client.current.send("/pub/chat/sendMessage", {}, JSON.stringify({
          "roomId": roomId,
          sender: nickname,
          message: message,
          type: 'TALK',
          roomType: roomType,
      }))
      setMessage('')
  }

	function getuserList(){
		axios.get(`http://localhost:8080/rooms/info?roomType=${roomType}&roomId=${roomId}`)
      .then(res=>{
        setUserlist(Object.values(res.data.userList))
      })
      .catch(err=>console.log(err))
	}

	return(
		<>
        <div className={style.chatting_container}>
          <div className={style.chatting_title + ' ' + style.chatting_common_box}>
            <h3>1. 채팅 앱</h3>
          </div>
          <div className={style.chatting_nickname + ' ' + style.chatting_common_box}>
            <h3>현재 닉네임: {nickname}</h3>
          </div>
          <div className={style.chatting_common_box + ' ' + style.chatting_btn_exit} onClick={()=>{
          client.current.disconnect();
          navigate(-1)
        }}>나가기</div>
          <div className={style.clear}>
        </div>
        <div className={style.chatting_room_container}>
          <div className={style.chatting_room_left}>
            <div style={{display:'grid', marginBottom:'0px', gridTemplateColumns: '1fr 1fr 1fr'}}>
              {
                [1, 2, 3, 4, 5, 6].map((data, index)=>(
                  <>
                  <div className={style.chatting_room_profile}>
                    <div className={style.chatting_profile_img}>.</div>
                    <div className={style.chatting_profile_info}>
                      <h3 className={style.profile_info_nickname}>{userlist[index]}</h3>
                      <h3 className={style.profile_info_roll}>{userlist[index] === ''}</h3>
                    </div>
                  </div>
                  </>
                ))
              }
            </div>
          </div>
          <div className={style.chatting_room_right}>
            
            <textarea className={style.chatting_chat_container + ' chat'} disabled={true}></textarea>
            <div className={style.chatting_chat_box}>
              <input className={style.chatting_chat_input} onChange={onMessageChange} onKeyDown={onKeyDown} value={message}/>
              <div className={style.chatting_chat_btn} onClick={sendMessage}>보내기</div>
            </div>
          </div>
          </div>
        </div>
		</>
	);
};

export default ChatRoom;