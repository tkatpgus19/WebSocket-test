import './App.css';
import { Routes, Route } from 'react-router-dom';
import WaitingRoom from './pages/WaitingRoom';
import ChatRoom from './pages/ChatRoom';
import Main from './pages/Main';
import { useEffect, useRef, useState } from 'react';
import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs';

function App() {
  return (
    <div className="App">
      <Routes>
        <Route path="/main" element={<Main/>}/>
        <Route path="/" element={<WaitingRoom/>}/>
        <Route path="/chat" element={<ChatRoom/>}/>
      </Routes>
    </div>
  );
}

export default App;
