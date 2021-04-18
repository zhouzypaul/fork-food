/**
 * Renders bubble displaying first character of username.
 */
function Bubble(props) {
  return (
    <div className="bubble">
      {props.user.charAt(0).toLowerCase()}
    </div>
  );
}

export default Bubble;