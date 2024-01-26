import './App.css';
import Main from 'sockjs-client/lib/main';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import WaitingRoom from './pages/WaitingRoom';

function App() {
  
  

  return (
    <div className="App">
      <div>안녕하세용</div>
      
      <Routes>
        <Route path="/" component={<Main/>}/>
        <Route path="/chat" element={<WaitingRoom/>}/>
      </Routes>
    </div>
  );
}

export default App;
