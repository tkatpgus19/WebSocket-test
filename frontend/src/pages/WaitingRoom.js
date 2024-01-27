import { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function WaitingRoom(){

  // 채팅방 목록
  // 받아온 채팅방 목록이 비어있는지 체크하는 플래그
  // 방 이름
  // 닉네임
  const [roomList, setRoomList] = useState([]);
  const [isVoid, setIsVoid] = useState(true);
  const [roomName, setRoomName] = useState('');
  const [nickname, setNickname] = useState('');


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

  const makeRoom = ()=>{
    axios.post(`http://localhost:8080/chat/createroom?name=${roomName}`)
      .then(res=>{
        getRoomList()
        setRoomName('')
        console.log('방이름: ' + roomName)
      })
  }
  const onKeyDown = (e)=>{
    if(e.keyCode === 13){
      makeRoom()
      setRoomName('')
    }
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
            <th>입장하기</th>
          </tr>
        </thead>
        <tbody>
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
                <td>
                  <button className='move' 
                  onClick={()=>{
                    navigate("/chat", {state:data.roomId})
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
      
      <div>
        <input placeholder='방 이름' onChange={onRoomNameChange} onKeyDown={onKeyDown} data={roomName}/>
        <button onClick={makeRoom} >방 만들기</button>
      </div>
      
      <input placeholder='유저 이름'/>
      <button>적용</button>
    </>
  );
};

export default WaitingRoom;