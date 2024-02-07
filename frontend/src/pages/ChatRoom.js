import { useEffect, useRef, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs';
import axios from 'axios';
import style from '../styles/WaitingRoom.module.css'

function ChatRoom(){
	useEffect(()=>{
    axios.get(`http://${SERVER_URL}/rooms/set-timer?roomId=${roomId}`)
        .then(res=>console.log(res))
		connectChat();
    (() => {
      window.addEventListener("beforeunload", preventClose);    
    })();

    return () => {
        window.removeEventListener("beforeunload", preventClose);
  };
	}, [])
	const navigate = useNavigate();


	let location = useLocation()
	const {roomId, nickname, roomType} = location.state


  const [message, setMessage] = useState('');
	const [userlist, setUserlist] = useState([]);
  const [readylist, setReadylist] = useState([]);
  const [timer, setTimer] = useState(0);
	

  // 새로고침 막기 변수
  const preventClose = (e) => {
    e.preventDefault();
    e.returnValue = ""; // chrome에서는 설정이 필요해서 넣은 코드
  }
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
	
  // const SERVER_URL = "ec2-3-39-233-234.ap-northeast-2.compute.amazonaws.com"
  const SERVER_URL = process.env.REACT_APP_API_URL

	const connectChat = ()=>{
    const socket = new SockJS(`http://${SERVER_URL}/ws-stomp`)
    client.current = Stomp.over(socket)
    client.current.connect({}, onConnected, onError); 
  }
        
  function onConnected(){
    client.current.subscribe('/sub/chat/room/' + roomId, onMessageReceived);
    client.current.subscribe('/sub/room/' + roomId + '/status', onStatusReceived);
    client.current.send("/pub/chat/enterUser", {}, JSON.stringify({type: 'ENTER', "roomId": roomId, sender: nickname, roomType: roomType}))
    client.current.subscribe(`/sub/timer/`+roomId, onTimerReceived);
    getUserList();
  }

  function onError(error) {
      alert('error')
  }
  function onTimerReceived(payload){
    setTimer(JSON.parse(payload.body));
  }

  function onMessageReceived(payload) {
      const chattingWindow = document.querySelector('.chat')
      var chat = JSON.parse(payload.body);
      console.log("전송받은 데이터: ", chat)
      var value = `${chattingWindow.value}\n[${chat.sender}]\n${chat.message}`
      chattingWindow.value = value;
      chattingWindow.scrollTop = chattingWindow.scrollHeight
			// getUserList()
  }

  function onStatusReceived(payload){
    // payload 받아서 레디 상태, 유저 상태 갱신
    setUserlist(Object.keys(JSON.parse(payload.body)))
    setReadylist(Object.values(JSON.parse(payload.body)))
  }

  function sendMessage(){
      client.current.send("/pub/chat/sendMessage", {}, JSON.stringify({
          "roomId": roomId,
          sender: nickname,
          message: message,
          type: 'TALK',
          roomType: roomType,
      }))
  }

	function getUserList(){
		axios.get(`http://${SERVER_URL}/rooms/userStatus?roomType=${roomType}&roomId=${roomId}`)
      .then(res=>{
        setUserlist(Object.keys(res.data))
        setReadylist(Object.values(res.data))
        console.log('머서너 일이고...\n'+Object.keys(res.data))
      })
      .catch(err=>console.log(err))
	}

  function getReady(){
    axios.put(`http://${SERVER_URL}/rooms/ready`, {
      "roomId": roomId,
      sender: nickname,
      message: 'ready',
      type: 'TALK',
      roomType: roomType,
  }).then(res=>console.log('레디 성공'))
  }

	return(
		<>
    {timer}
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
                  <div key={userlist[index]} className={style.chatting_room_profile}>
                    <div className={style.chatting_profile_img}>.</div>
                    <div className={style.chatting_profile_info}>
                      <h3 className={style.profile_info_nickname}>{userlist[index]}</h3>
                      <h3 className={style.profile_info_roll}>{readylist[index]}</h3>
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
              <div className={style.chatting_room_ready} onClick={getReady}>READY</div>
          </div>
          </div>
        </div>
		</>
	);
};

export default ChatRoom;