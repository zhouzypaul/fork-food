import './App.css';
import {
  BrowserRouter as Router,
  Route
} from "react-router-dom";
import Start from './components/Start.js'
import Login from "./components/Login";
import Home from "./components/Home";

function App() {
  return (
    <div className="App">
      <Router>
        <Route exact path="/" component={Start}/>
        <Route path="/login" component={Login}/>
        <Route path="/home" component={Home}/>
      </Router>
    </div>
  );
}

export default App;
