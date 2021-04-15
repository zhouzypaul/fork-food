import './App.css';
import {
  BrowserRouter as Router,
  Route
} from "react-router-dom";
import Start from './components/Start.js'
import Login from "./components/Login";
import Home from "./components/Home";
import Profile from "./components/Profile";
import Settings from "./components/Settings";
import {useSelector} from "react-redux";
import Host from "./components/Host";
import Join from "./components/Join";
import NewUser from './components/NewUser';
import Survey from './components/Survey';
import Swipe from "./components/Swipe";
import Result from "./components/Result";
import About from "./components/About";

function App() {
  const user = useSelector(state => state.user);

  return (
    <div className="App">
      <Router>
        <Route exact path="/" component={Start} />
        <Route path="/about" component={About} />
        <Route path="/login" component={Login} />
        <Route path="/newUser" component={NewUser} />
        <Route path="/home" component={user !== "" ? Home : Start} />
        <Route path="/profile" component={user !== "" ? Profile : Start} />
        <Route path="/settings" component={user !== "" ? Settings : Start} />
        <Route path="/join" component={user !== "" ? Join : Start} />
        <Route path="/survey" component={user !== "" ? Survey : Start} />
        <Route path="/swipe" component={user !== "" ? Swipe : Start} />
        <Route path="/result" component={user !== "" ? Result : Start} />
        <Route path="/room/:roomId" component={user !== "" ? Host : Start} />
      </Router>
    </div>
  );
}

export default App;
