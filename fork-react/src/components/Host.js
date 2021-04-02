import TopBar from "./TopBar";

function Host() {
  const code = Math.floor(1000 + Math.random() * 9000);
  return (
    <>
      <TopBar to="/home" showOptions={true}/>
      <div className="content">
        <div className="title-text">
          share the code
        </div>
        <div className="code">{code}</div>
        <button className="primary-button">start</button>
      </div>
    </>
  );
}

export default Host;