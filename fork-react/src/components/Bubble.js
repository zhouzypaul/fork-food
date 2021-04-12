function Bubble(props) {
  return (
    <div className="bubble">
      {props.user.charAt(0)}
    </div>
  );
}

export default Bubble;