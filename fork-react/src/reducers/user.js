const userReducer = (state = "", action) => {
  switch (action.type) {
    case 'LOGIN' :
      return action.payload;
    case 'LOGOUT' :
      return "";
    default :
      return "";
  }
}

export default userReducer;