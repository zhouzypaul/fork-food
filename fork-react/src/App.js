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
import { useSelector } from "react-redux";
import Host from "./components/Host";
import Join from "./components/Join";
import NewUser from './components/NewUser';

function App() {
  const user = useSelector(state => state.user);
  return (
    <div className="App">
      <Router>
        <Route exact path="/" component={Start} />
        <Route path="/login" component={Login} />
        <Route path="/newuser" component={NewUser} />

        {/* checking if user is logged in, should we consider using JWT or something else more secure? */}
        <Route path="/home" component={user !== "" ? Home : Start} />
        <Route path="/profile" component={user !== "" ? Profile : Start} />
        <Route path="/settings" component={user !== "" ? Settings : Start} />
        <Route path="/host" component={user !== "" ? Host : Start} />
        <Route path="/join" component={user !== "" ? Join : Start} />
      </Router>
    </div>
  );
}

export default App;
