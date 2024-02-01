import { useEffect, useRef, useState } from 'react';
import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function WaitingRoom(){
  useEffect(()=>{
    // getRoomList();
    axios.get('http://localhost:8080/rooms/normal')
    .then(res=>setRoomList(res.data))

    // 방이 실시간으로 바뀌는 것을 인지하기 위해 항상 해당 채널을 구독해야한다.
    connectSession();
  }, [])

  const client = useRef();

  const connectSession = ()=>{
    const socket = new SockJS('http://localhost:8080/ws-stomp')
    client.current = Stomp.over(socket)
    client.current.connect({}, onConnected, onError); 
  }
        
  function onConnected(){
      client.current.subscribe(`/sub/${roomType}/room-list`, onRoomInfoReceived);
    }
    function onError(error) {
      alert('error')
    }

    function onRoomInfoReceived(payload){
      // const chattingWindow = document.querySelector('.chat')
      console.log(JSON.parse(payload.body))
      setRoomList(JSON.parse(payload.body))
    }
    function test(){
    //   client.current.send('/pub/make-room', {}, JSON.stringify({
    //     roomType: roomType,
    //     roomName: roomName,
    //     isLocked: isLocked,
    //     roomPassword: roomPassword,
    //     problemTier: problemTier,
    //     problemNo: problemNo,
    //     timeLimit: timeLimit,
    //     language: language,
    //     hasReview: hasReview,
    // }))
    axios.post('http://localhost:8080/rooms', {
      roomType: roomType,
      roomName: roomName,
      isLocked: isLocked,
      roomPassword: roomPassword,
      problemTier: problemTier,
      problemNo: problemNo,
      timeLimit: timeLimit,
      language: language,
      hasReview: hasReview,
  })
  .then(res=>{
    navigate("/chat", {state: {roomId:res.data, nickname: nickname, roomType: roomType}})
  })
    }
    

  // 채팅방 목록
  // 받아온 채팅방 목록이 비어있는지 체크하는 플래그
  // 방 이름
  // 닉네임
  const [roomList, setRoomList] = useState([]);
  const [isVoid, setIsVoid] = useState(true);
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

  const [nowRoomType, setNowRoomType] = useState('Normal')

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
  // 

  const navigate = useNavigate();

  const getRoomList = ()=>{
    axios.get('http://localhost:8080')
      .then(res=>{
        console.log(res.data)
        if(res.data[0] !== undefined){
          setIsVoid(false)
        }
        var tmp = res.data
        setRoomList(tmp)
      })
      .catch(err=>console.log(err))
  };

  const onRoomNameChange = (e)=>{
    setRoomName(e.target.value)
  }

  const onNicknameChange = (e)=>{
    setNickname(e.target.value)
  }
  const onKeyDown = (e)=>{
    if(e.keyCode === 13){
      // makeRoom()
      setRoomName('')
    }
  }
  const onRoomPwdChange = (e)=>{
    setRoomPassword(e.target.value)
  }
  const onMaxUserCntChange = (e)=>{
    setMaxUserCnt(e.target.value)
  }
  
  return(
    <>
      <input onChange={onNicknameChange} placeholder='유저 이름'/>
      <button onClick={()=>{setNowRoomType("normal");client.current.unsubscribe();
      axios.get('http://localhost:8080/rooms/normal')
        .then(res=>setRoomList(res.data))
        client.current.connect({}, onConnected, onError); }}>노말전</button>
      <button onClick={()=>{setNowRoomType("item");client.current.unsubscribe();
      axios.get('http://localhost:8080/rooms/item')
        .then(res=>setRoomList(res.data))
        client.current.connect({}, onConnected, onError); }}>아이템전</button>
      <h1>현재방: {nowRoomType}</h1>
      <table style={{marginTop: '20px', marginBottom: '20px'}}>
        <thead>
          <tr>
            <th>채팅방 번호</th>
            <th>채팅방 이름</th>
            <th>비밀방 여부</th>
            <th>현재 인원</th>
            <th>입장하기</th>
          </tr>
        </thead>
        <tbody>
          {
            isVoid ? <tr><td colSpan={'5'}>채팅방이 존재하지 않습니다.</td></tr> : null
          }
          {
            roomList.map((data, index)=>{
            return(
              <>
              <tr key={index}>
                <td>{index}</td>
                <td>{data.roomName}</td>
                <td>{data.isLocked ? 'Y' : 'N'}</td>
                <td>{data.userCnt+'/'+data.maxUserCnt}</td>
                <td>
                  <button className='move' 
                  onClick={()=>{
                    // if(data.secretChk){                      
                    //   const passwd = prompt("비밀번호")
                    //   axios.post(`http://localhost:8080/chat/checkPwd?roomId=${data.roomId}&roomPwd=${passwd}`)
                    //     .then(res=>{
                    //       if(res.data){
                    //         axios.get(`http://localhost:8080/chat/room/${data.roomId}`)
                    //           .then(res=>{
                    //             if(res.data.maxUserCnt > res.data.userCount){
                    //               navigate("/chat", {state: {roomId:data.roomId, nickname: nickname}})
                    //             }
                    //             else{
                    //               alert('인원이 가득 찼습니다.')
                    //             }
                    //             console.log(res)
                    //           })
                    //        }
                    //       else{
                    //         alert('비밀번호가 다름')
                    //       }})
                    // }
                    // else{
                    //   axios.get(`http://localhost:8080/chat/room/${data.roomId}`)
                    //           .then(res=>{
                    //             if(res.data.maxUserCnt > res.data.userCount){
                    //               navigate("/chat", {state: {roomId:data.roomId, nickname: nickname}})
                    //             }
                    //             else{
                    //               alert('인원이 가득 찼습니다.')
                    //             }
                    //             console.log(res)
                    //           })
                    // }
                    console.log("룸 아이디: "+data.roomId)
                    navigate("/chat", {state: {roomId:data.roomId, nickname: nickname, roomType: nowRoomType}})
                    }}>
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
      <div>
        <button onClick={getRoomList}>방목록 불러오기</button>
      </div>
      
      <div style={{border: '1px solid wheat', width: '50%', margin:'50px auto'}}>
      <p style={{margin:'0'}}>방 타입</p><input placeholder='방 이름' onChange={onRoomTypeChange} onKeyDown={onKeyDown} value={roomType}/>
      <p style={{margin:'0'}}>방 이름</p><input placeholder='방 이름' onChange={onRoomNameChange} onKeyDown={onKeyDown} value={roomName}/>
      <p style={{margin:'0'}}>비밀방 여부</p>
        <input type='checkbox' onChange={()=>{setIsLocked(!isLocked);}} value={isLocked}/><br/>
        {
          isLocked ? <><p style={{margin:'0'}}>비밀번호</p><input placeholder='비밀번호' onChange={onRoomPwdChange} value={roomPassword}/></> : null
        }<br/>
      <p style={{margin:'0'}}>문제 티어</p><input placeholder='문제 티어' onChange={onProblemTierChange} onKeyDown={onKeyDown} value={problemTier}/>
      <p style={{margin:'0'}}>문제 번호</p><input type='number' placeholder='문제 번호' onChange={onProblemNoChange} onKeyDown={onKeyDown} value={problemNo}/>
      <p style={{margin:'0'}}>시간 제한</p><input type='number' placeholder='시간 제한' onChange={onTimeLimitChange} onKeyDown={onKeyDown} value={timeLimit}/>
      <p style={{margin:'0'}}>풀이 언어</p><input placeholder='풀이 언어' onChange={onLanguageChange} onKeyDown={onKeyDown} value={language}/>
      <p style={{margin:'0'}}>리뷰 여부</p><input type='checkbox' onChange={()=>{setHasReview(!hasReview);}} value={hasReview}/><br/>
    
        
{/*         
        <p>인원 제한</p>
        <input type='number' placeholder='1명' onChange={onMaxUserCntChange} value={maxUserCnt} min={2} max={6}/><br/> */}
        <button onClick={()=>{setRoomPassword(''); setIsLocked(false);
      test();
      console.log(`roomType=${roomType}roomName=${roomName}isLocked=${isLocked}roomPassword=${roomPassword}problemTier=${problemTier}problemNo=${problemNo}timeLimit=${timeLimit}language=${language}hasReview=${hasReview}`)}} >방 만들기</button>

      </div>
      
    </>
  );
};

export default WaitingRoom;