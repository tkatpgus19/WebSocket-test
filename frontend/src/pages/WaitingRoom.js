import { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function WaitingRoom(){
  useEffect(()=>{
    getRoomList();
  }, [])
  // 채팅방 목록
  // 받아온 채팅방 목록이 비어있는지 체크하는 플래그
  // 방 이름
  // 닉네임
  const [roomList, setRoomList] = useState([]);
  const [isVoid, setIsVoid] = useState(true);
  const [roomName, setRoomName] = useState('');
  const [nickname, setNickname] = useState('닉네임1');
  const [secretChk, setSecretChk] = useState(false);
  const [roomPwd, setRoomPwd] = useState('');
  const [maxUserCnt, setMaxUserCnt] = useState(2);

 
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

  const makeRoom = ()=>{
    if(roomName === ''){
      alert('방 이름을 입력하세요!')
    }
    else{
      console.log(maxUserCnt)
      axios.post(`http://localhost:8080/chat/createroom?name=${roomName}&maxUserCnt=${maxUserCnt}&roomPwd=${roomPwd}&secretChk=${secretChk}`)
        .then(res=>{
          getRoomList()
          setRoomName('')
          console.log('방이름: ' + roomName)
      })
    }
  }
  const onKeyDown = (e)=>{
    if(e.keyCode === 13){
      makeRoom()
      setRoomName('')
    }
  }
  const onRoomPwdChange = (e)=>{
    setRoomPwd(e.target.value)
  }
  const onMaxUserCntChange = (e)=>{
    setMaxUserCnt(e.target.value)
  }
  
  return(
    <>
      <h1>채팅 앱</h1>
      <h3>현재 닉네임: {nickname}</h3>
      
      <input onChange={onNicknameChange} placeholder='유저 이름'/>
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
                <td>{data.secretChk ? 'Y' : 'N'}</td>
                <td>{data.userCount+'/'+data.maxUserCnt}</td>
                <td>
                  <button className='move' 
                  onClick={()=>{
                    if(data.secretChk){                      
                      const passwd = prompt("비밀번호")
                      axios.post(`http://localhost:8080/chat/checkPwd?roomId=${data.roomId}&roomPwd=${passwd}`)
                        .then(res=>{
                          if(res.data){
                            axios.get(`http://localhost:8080/chat/room/${data.roomId}`)
                              .then(res=>{
                                if(res.data.maxUserCnt > res.data.userCount){
                                  navigate("/chat", {state: {roomId:data.roomId, nickname: nickname}})
                                }
                                else{
                                  alert('인원이 가득 찼습니다.')
                                }
                                console.log(res)
                              })
                           }
                          else{
                            alert('비밀번호가 다름')
                          }})
                    }
                    else{
                      axios.get(`http://localhost:8080/chat/room/${data.roomId}`)
                              .then(res=>{
                                if(res.data.maxUserCnt > res.data.userCount){
                                  navigate("/chat", {state: {roomId:data.roomId, nickname: nickname}})
                                }
                                else{
                                  alert('인원이 가득 찼습니다.')
                                }
                                console.log(res)
                              })
                    }
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
        <p>방 이름</p>
        <input placeholder='방 이름' onChange={onRoomNameChange} onKeyDown={onKeyDown} value={roomName}/><br/>
        <p>비밀방 여부</p>
        <input type='checkbox' onChange={()=>{setSecretChk(!secretChk);}} value={secretChk}/><br/>
        {
          secretChk ? <><p>비밀번호</p><input placeholder='비밀번호' onChange={onRoomPwdChange} value={roomPwd}/></> : null
        }<br/>
        <p>인원 제한</p>
        <input type='number' placeholder='1명' onChange={onMaxUserCntChange} value={maxUserCnt} min={2} max={6}/><br/>
        <button onClick={()=>{makeRoom(); setRoomPwd(''); setSecretChk(false)}} >방 만들기</button>
      </div>
    </>
  );
};

export default WaitingRoom;