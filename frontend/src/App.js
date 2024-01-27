import './App.css';
import { Routes, Route } from 'react-router-dom';
import WaitingRoom from './pages/WaitingRoom';
import ChatRoom from './pages/ChatRoom';

function App() {
  return (
    <div className="App">
      <Routes>
        <Route path="/" element={<WaitingRoom/>}/>
        <Route path="/chat" element={<ChatRoom/>}/>
      </Routes>
    </div>
  );
}

export default App;
