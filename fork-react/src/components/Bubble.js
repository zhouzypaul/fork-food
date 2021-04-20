/**
 * Renders bubble displaying first character of username.
 */
function Bubble(props) {
  return (
    <div className="bubble" style={{backgroundColor: props.host ? "#FFC6BA" : "#A5D4FF"}}>
      {props.user.charAt(0).toLowerCase()}
    </div>
  );
}

export default Bubble;