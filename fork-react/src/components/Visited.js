import {useEffect, useState} from "react";
import axios from "axios";

const SERVER_URL = 'http://localhost:4567';
const GOOGLE = "https://www.google.com/search?q=";
const MS_IN_DAY = 1000 * 60 * 60 * 24;

function Visited(props) {
  const [recent, setRecent] = useState([]);

  const getRecent = () => {
    const toSend = {
      username: props.user,
    };

    let config = {
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
    };

    const getTime = (date) => {
      console.log(localStorage);
      const present = new Date();
      const old = new Date(date);
      const delta = present.getTime() - old.getTime();
      console.log(delta)
      const days = Math.floor(delta / MS_IN_DAY);
      if (days === 0) {
        return "today";
      } else if (days === 1) {
        return "yesterday";
      } else if (days < 7) {
        return days + " days ago";
      } else if (days < 30) {
        const weeks = Math.floor(days / 7);
        const unit = weeks === 1 ? " week" : " weeks"
        return weeks + unit + " ago";
      } else if (days < 365) {
        const months = Math.floor(days / 30);
        const unit = months === 1 ? " month" : " months"
        return months + unit + " ago";
      } else {
        const years = Math.floor(days / 365);
        const unit = years === 1 ? " year" : " years"
        return years + unit + " ago";
      }
    }

    axios.post(`${SERVER_URL}/getMostRecentRests`, toSend, config)
      .then((response) => {
        if (response.data['err'].length === 0) {
          const rest = response.data["restaurants"];
          const display = [];
          let i = 0;
          rest.forEach((r) => {
            const restKey = r['business_id'] + props.user;
            if (i < 5) {
              const last = localStorage.getItem(restKey);
              const param = r.name.replace(/\s/g, "+") + "+" + r.city + "+" + r.state;
              display.push(<a href={GOOGLE + param} target="_blank" className="links" rel="noreferrer" key={i++}>
                <div className="recent">
                  <i className="material-icons-outlined">restaurant</i>
                  <div className="map-text">{r.name}</div>
                  <div className="days">{getTime(last)}</div>
                </div>
              </a>);
            } else {
              localStorage.removeItem(restKey);
            }
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
      <div className="title-text">recent</div>
      <div className="scroll-box">
        {recent}
      </div>
    </div>
  );
}

export default Visited;