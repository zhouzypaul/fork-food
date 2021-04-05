import userReducer from "./user";
import { combineReducers } from "redux";

const rootReducer = combineReducers({
  user: userReducer
})

const socketConn = new WebSocket("ws://localhost:4567/socket")

export { rootReducer, socketConn };