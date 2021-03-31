const userReducer = (state = "", action) => {
  switch (action.type) {
    case 'LOGIN' :
      state = action.payload;
      return state;
    case 'LOGOUT' :
      state = "";
      return state;
    default :
      return "";
  }
}

export default userReducer;