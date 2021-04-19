function Popup(props) {

  return (
    <div className="popup">
      <div className="popup-content">
        <div className="links" id="close" onClick={props.toggle}>&times;</div>
        {props.message}
        {props.button ? <button className="primary-button" onClick={props.click}>{props.button}</button> : null}
      </div>
    </div>
  );
}

export default Popup;