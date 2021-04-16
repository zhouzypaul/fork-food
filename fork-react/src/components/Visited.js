import {useEffect, useState} from "react";
import axios from "axios";
import {login} from "../actions";

const SERVER_URL = 'http://localhost:4567';
const GOOGLE = "https://www.google.com/search?q=";

function Visited(props) {
  const [recent, setRecent] = useState();

  const getRecent = () => {
    const toSend = {
      // username and password
      username: props.user,
    };

    let config = {
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
    };

    axios.post(`${SERVER_URL}/getMostRecentRests`, toSend, config)
      .then((response) => {
        if (response.data['err'].length === 0) {
          const rest = response.data["restaurants"];
          const display = [];
          rest.forEach((r) => {
            const param = rest.name.replace(/\s/g, "+") + "+" + rest.city + "+" + rest.state;
            display.push(<a href={GOOGLE + param} target="_blank" className="links">
                <div className="recent">
                  {r.name}
                </div>
              </a>);
          });
          setRecent(display);
        } else {
          alert(response.data['err']);
        }
      })
      .catch(function (error) {
        console.log(error);
      });
  }

  useEffect(() => {
    getRecent();
  }, [])

  return (
    <div className="content" id="visited-section">
      <div className="title-text">favorites</div>
      <div className="scroll-box">
      </div>
      <div className="title-text">recent</div>
      <div className="scroll-box">
        {recent}
      </div>
    </div>
  );
}

export default Visited;