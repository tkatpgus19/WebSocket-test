import { useRef, useState } from 'react';
import axios from 'axios';
import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs';

function WaitingRoom(){

    // 채팅방 목록
  // 받아온 채팅방 목록이 비어있는지 체크하는 플래그
  // 방 이름
  // 채팅 메시지
  // 닉네임
  const [roomList, setRoomList] = useState([]);
  const [isVoid, setIsVoid] = useState(true);
  const [roomName, setRoomName] = useState('');
  const [message, setMessage] = useState('');
  const [nickname, setNickname] = useState('');

  // stomp 세션 연결 유지를 위한 변수
  const client = useRef();

  // 
  let roomId = ''
  const [roomNo, setRoomNo] = useState('')

  const getRoomList = ()=>{
    axios.get('http://localhost:8080')
      .then(res=>{
        console.log(res.data)
        if(res.data[0] != undefined){
          setIsVoid(false)
        }
        var tmp = res.data
        setRoomList(tmp)
      })
      .catch(err=>console.log(err))
  };

  const onChange = (e)=>{
    setRoomName(e.target.value)
  }
  const onChange2 = (e)=>{
    setMessage(e.target.value)
  }

  const makeRoom = ()=>{
    axios.post(`http://localhost:8080/chat/createroom?name=${roomName}`)
      .then(res=>{
        getRoomList()
        setRoomName('')
        console.log('방이름: ' + roomName)
      })
  }
  const onKeyDown = (e)=>{
    if(e.keyCode == 13){
      makeRoom()
      setRoomName('')
    }
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
      //console.log("payload 들어오냐? :"+payload);
      const chattingWindow = document.querySelector('.chat')
      var chat = JSON.parse(payload.body);
      console.log("전송받은 데이터: ", chat.message)
      var value = chattingWindow.value + '\n' + chat.message
      chattingWindow.value = value;
  }

  function sendMessage(){
      client.current.send("/pub/chat/sendMessage", {}, JSON.stringify({
          "roomId": roomNo,
          sender: 'kim',
          message: message,
          type: 'TALK'
      }))
      setMessage('')
  }

  function onClick2(){

  }
    return(
        <>
            <h1>채팅 앱</h1>
        <h3>현재 닉네임: {nickname}</h3>
        <table>
            <thead>
            <tr>
                <th>채팅방 번호</th>
                <th>채팅방 이름</th>
                <th>비고</th>
            </tr>
            {
                isVoid ? <tr><td colSpan={'3'}>채팅방이 존재하지 않습니다.</td></tr> : null
            }
            
            {
                roomList.map((data, index)=>{
                return(
                    <>
                    <tr key={index}>
                        <td>{index}</td>
                        <td>{data.roomName}</td>
                        <td><button className='move' onClick={()=>{roomId = data.roomId; setRoomNo(roomId); connectChat();}}>이동하기</button></td>
                    </tr>
                    </>
                )
                })
            }
            </thead>
            
        </table>
        <div>
            <button onClick={getRoomList}>방목록 불러오기</button>
        </div>
        
        <div>
            <input placeholder='방 이름' onChange={onChange} onKeyDown={onKeyDown} data={roomName}/>
            <button onClick={makeRoom} >방 만들기</button>
        </div>
        
        <input placeholder='유저 이름'/>
        <button onClick={onClick2}>적용</button>

    <div></div>
        <textarea className='chat' ></textarea>
        <input onChange={onChange2}/>
        <button onClick={sendMessage}>메시지 전송</button>
        </>
    );
};

export default WaitingRoom;