function Popup(props) {
  const home = () => {
    window.location.href = props.to;
  }

  return (
    <div className="popup">
      <div className="popup-content">
        <div className="links" id="close" onClick={props.toggle}>&times;</div>
        {props.message}
        <br/>
        <br/>
        <button className="primary-button" onClick={home}>home</button>
      </div>
    </div>
  );
}

export default Popup;