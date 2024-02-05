import { useEffect, useRef, useState } from 'react';
import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function WaitingRoom(){
  useEffect(()=>{

    // 서버로 방목록 조회 api 요청(기본이 노멀전 조회)
    axios.get('http://172.30.1.37:8080/rooms/normal')
    .then(res=>setRoomList(res.data))

    // 방이 실시간으로 바뀌는 것을 인지하기 위해 항상 해당 채널을 구독해야한다.
    connectSession();
  }, [])

  const client = useRef();
  let rt = 'normal';

  // 세션 연결
  const connectSession = ()=>{
    const socket = new SockJS('http://172.30.1.37:8080/ws-stomp')
    client.current = Stomp.over(socket)
    client.current.connect({}, onConnected, onError); 
  }
        
  // 세션이 연결되는 순간 방 리스트를 출력하는 토픽과 전체 채팅 관련 토픽을 구독해서 메시지를 기다린다.
  function onConnected(){
    client.current.subscribe(`/sub/${rt}/room-list`, onRoomInfoReceived);
    client.current.subscribe('/sub/chat/all', onChatReceived)
  }

  // 에러 처리 콜백 함수
  function onError(error) {
    alert('error')
  }

  // 방 리스트를 수신했을 때, 방 목록 갱신
  function onRoomInfoReceived(payload){
    setRoomList(JSON.parse(payload.body))
  }

  // 채팅 매시지를 수신했을 때, 채팅 표시
  function onChatReceived(payload){
    const chattingWindow = document.querySelector('.chat2')
    var chat = JSON.parse(payload.body);
    var value = `${chattingWindow.value}\n[${chat.sender}]\n${chat.message}`
    chattingWindow.value = value;
    chattingWindow.scrollTop = chattingWindow.scrollHeight
    setTextarea(JSON.parse(payload.body).message)
  }

  // 채팅 메시지 전송
  function sendChat(){
    client.current.send("/pub/chat/all/sendMessage", {}, JSON.stringify({
      "roomId": 'all',
      sender: nickname,
      message: chat,
      type: 'TALK',
      roomType: roomType,
  }))
  }

  // 게임방 생성 함수
  // 생성 후 방으로 이동한다.
  function makeRoom(){

    if(nickname === '닉네임1'){
      alert('닉네임을 바꿔주세요')
    }
    else{
    // 방 생성 api 호출
    axios.post('http://172.30.1.37:8080/rooms', {
      roomType: roomType,
      roomName: roomName,
      hasPassword: isLocked,
      roomPassword: roomPassword,
      problemTier: problemTier,
      problemNo: problemNo,
      timeLimit: timeLimit,
      language: language,
      hasReview: hasReview,
      master: nickname
    })
    .then(res=>{
      // 방 생성에 성공하면 대기방 화면으로 이동
    
        navigate("/chat", {state: {roomId:res.data, nickname: nickname, roomType: roomType}})
      
    })
  }
  }


  // 찾기를 원하는 방 종류를 선택 시 새로 소켓 연결 및 방목록 조회
  function changeRoomType(roomType){
    client.current.disconnect();
    setNowRoomType(roomType); 

    rt = roomType;

    // 방 목록 다시 조회
    axios.get(`http://172.30.1.37:8080/rooms/${roomType}`)
      .then(res=>{
        setRoomList(res.data); 
        connectSession()})
  }

  function checkMaxCnt(cnt, maxCnt){
    if(cnt >= maxCnt){
      return false;
    }
    return true;
  }

  // 방목록을 뿌려주기 위한 state
  const [roomList, setRoomList] = useState([]);

  const [roomName, setRoomName] = useState('');
  const [nickname, setNickname] = useState('닉네임1');
  const [isLocked, setIsLocked] = useState(false);
  const [roomPassword, setRoomPassword] = useState('');
  const [maxUserCnt, setMaxUserCnt] = useState(2);
  const [roomType, setRoomType] = useState('normal');
  const [problemTier, setProblemTier] = useState('골드1');
  const [problemNo, setProblemNo] = useState(1001);
  const [timeLimit, setTimeLimit] = useState(100);
  const [language, setLanguate] = useState('JAVA');
  const [hasReview, setHasReview] = useState(false)

  const [textarea2, setTextarea] = useState('');
  const [nowRoomType, setNowRoomType] = useState('normal')

  const [chat, setChat] = useState('');

  const onRoomTypeChange = (e)=>{
    setRoomType(e.target.value)
  }
  const onProblemTierChange = (e)=>{
    setProblemTier(e.target.value)
  }
  const onProblemNoChange = (e)=>{
    setProblemNo(e.target.value)
  }
  const onTimeLimitChange = (e)=>{
    setTimeLimit(e.target.value)
  }
  const onLanguageChange = (e)=>{
    setLanguate(e.target.value)
  }
  const onHasReviewChange = (e)=>{
    setHasReview(e.target.value)
  }

  const navigate = useNavigate();

  const onRoomNameChange = (e)=>{
    setRoomName(e.target.value)
  }

  const onNicknameChange = (e)=>{
    setNickname(e.target.value)
  }
  const onRoomPwdChange = (e)=>{
    setRoomPassword(e.target.value)
  }
  const [test, setTest]= useState('')
  return(
    <>
      <input onChange={onNicknameChange} placeholder='유저 이름'/>
      <button onClick={()=>{changeRoomType('normal')}}>노말전</button>
      <button onClick={()=>{changeRoomType('item')}}>아이템전</button>
      <h1>현재방: {nowRoomType}</h1>

      <div style={{width: '50%', background: 'white', width:'fit-content', margin: 'auto'}}>
        <table style={{width: '100%'}}>
          <thead>
            <tr>
              <th>채팅방 번호</th>
              <th>채팅방 이름</th>
              <th>비밀방 여부</th>
              <th>리뷰 여부</th>
              <th>현재 인원</th>
              <th>입장하기</th>
            </tr>
          </thead>
          <tbody>
            {
              !roomList[0] ? <tr><td colSpan={'6'}>채팅방이 존재하지 않습니다.</td></tr> : null
            }
            {
              roomList.map((data, index)=>{
                console.log(data)
              return(
                <>
                <tr>
                  <td>{index}</td>
                  <td>{data.roomName}</td>
                  <td>{data.hasPassword ? 'Y' : 'N'}</td>
                  <td>{data.hasReview ? 'Y' : 'N'}</td>
                  <td>{data.userCnt+'/'+data.maxUserCnt}</td>
                  <td>
                    <button className='move' 
                    onClick={()=>{
                      if(data.hasPassword){                      
                        const passwd = prompt("비밀번호")
                        axios.post(`http://172.30.1.37:8080/rooms/checkPwd?roomType=${data.roomType}&roomId=${data.roomId}&roomPwd=${passwd}`)
                          .then(res=>{
                            if(res.data){
                              checkMaxCnt() ? navigate("/chat", {state: {roomId:data.roomId, nickname: nickname, roomType: nowRoomType}}) : alert('방 인원 가득 참')
                            }
                            else{
                              alert('비밀번호가 다름')
                            }})
                      }
                      else{
                        if(checkMaxCnt()){
                          if(nickname === '닉네임1'){
                            alert('닉네임을 바꿔주세요')
                          }else{
                            navigate("/chat", {state: {roomId:data.roomId, nickname: nickname, roomType: roomType}})
                          }
                        } else{
                          alert('방 인원 가득 참') 
                        }
                      }}}>
                      이동하기
                    </button>
                  </td>
                </tr>
                </>
              )
              })
            }
          </tbody>
        </table>
      </div>
      <div style={{width: '50%', border: '1px solid wheat', width: '30%', margin:'50px auto', background: '#61dafb', padding: '50px', border: '3px solid black'}}>
      <p style={{margin:'0'}}>방 타입</p><input placeholder='방 이름' onChange={onRoomTypeChange} value={roomType}/>
      <p style={{margin:'0'}}>방 이름</p><input placeholder='방 이름' onChange={onRoomNameChange} value={roomName}/>
      <p style={{margin:'0'}}>비밀방 여부</p>
        <input type='checkbox' onChange={()=>{setIsLocked(!isLocked);}} value={isLocked}/><br/>
        {
          isLocked ? <><p style={{margin:'0'}}>비밀번호</p><input placeholder='비밀번호' onChange={onRoomPwdChange} value={roomPassword}/></> : null
        }<br/>
      <p style={{margin:'0'}}>문제 티어</p><input placeholder='문제 티어' onChange={onProblemTierChange} value={problemTier}/>
      <p style={{margin:'0'}}>문제 번호</p><input type='number' placeholder='문제 번호' onChange={onProblemNoChange} value={problemNo}/>
      <p style={{margin:'0'}}>시간 제한</p><input type='number' placeholder='시간 제한' onChange={onTimeLimitChange} value={timeLimit}/>
      <p style={{margin:'0'}}>풀이 언어</p><input placeholder='풀이 언어' onChange={onLanguageChange} value={language}/>
      <p style={{margin:'0'}}>리뷰 여부</p><input type='checkbox' onChange={onHasReviewChange} value={hasReview}/><br/>

      <input type='checkbox' value='노말' onClick={()=>{setTest('노말'); alert(test)}}/>
        
{/*         
        <p>인원 제한</p>
        <input type='number' placeholder='1명' onChange={onMaxUserCntChange} value={maxUserCnt} min={2} max={6}/><br/> */}
        <button onClick={()=>{
          setRoomPassword(''); 
          setIsLocked(false); 
          makeRoom();}}>방 만들기</button>

      </div>

      <textarea className='chat2' style={{height: '500px', color:'black'}} value={textarea2}></textarea>
      <input onChange={e=>{setChat(e.target.value)}}></input><button onClick={sendChat}>전송</button>
      
    </>
  );
};

export default WaitingRoom;