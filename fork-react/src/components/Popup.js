function Popup(props) {
  return (
    <div className="popup">
      <div className="popup-content">
        <div className="links" id="close" onClick={props.toggle}>&times;</div>
        {props.message}
        <button className="primary-button" onClick={props.click}>{props.button}</button>
      </div>
    </div>
  );
}

export default Popup;