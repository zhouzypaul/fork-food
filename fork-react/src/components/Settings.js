import TopBar from "./TopBar";

function Settings() {
  return (
    <>
      <TopBar to="/home" showOptions={false}/>
      <div className="content">
        <div className="title-text">
          settings
        </div>
      </div>
    </>
  );
}

export default Settings;