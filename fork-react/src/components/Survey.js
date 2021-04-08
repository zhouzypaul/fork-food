import {useState, useEffect, useRef} from "react";
import {Link} from "react-router-dom";
import TopBar from "./TopBar";
import {useSelector} from "react-redux";
import axios from 'axios';

const TYPES = ["coffee & tea", "chinese", "pizza", "italian", "japanese", "indian", "greek",
  "middle eastern", "pizza", "vegan", "mexican", "thai", "american", "salad", "barbeque", "seafood",
  "steak", "vietnamese", "breakfast", "dessert"];
const PRICES = ["$", "$$", "$$$"];

function Option(props) {
  const selected = useRef(false);
  const toggle = (e) => {
    props.pick(e.target.innerText);
    selected.current = !selected.current;
    if (selected.current) {
      e.target.style.backgroundColor = "#DDAFF980";
    } else {
      e.target.style.backgroundColor = "gainsboro";
    }
  }

  return(
    <button className="survey-box" onClick={toggle} style={{backgroundColor: props.color}}>
      {props.value}
    </button>
  );
}

function Survey() {
  const [types, setTypes] = useState([]);
  const [prices, setPrices] = useState([]);
  const [radius, setRadius] = useState(2);
  const [unit, setUnit] = useState("miles");
  const selectedTypes = useRef({});
  const priceRange = useRef({});
  const user = useSelector(state => state.user);

  const generateObjects = (options, ref) => {
    let selections = {};
    options.forEach(op => {selections[op] = false});
    ref.current = selections;
  }

  const generateBoxes = (options, handler, setter) => {
    const boxes = [];
    let i = 1;
    for (let op in options) {
      if (options.hasOwnProperty(op)) {
        boxes.push(<Option value={op} color={options[op] ? "#DDAFF980" : "gainsboro"} pick={handler} key={i}/>);
        i++;
      }
    }
    setter(boxes);
  }

  const getPreferences = () => {
    const toSend = {
      username: user
    }

    let config = {
      headers: {
        "Content-Type": "application/json",
        'Access-Control-Allow-Origin': '*',
      }
    }

    axios.post("http://localhost:4567/getUserPref", toSend, config)
      .then(response => {
        const data = response.data;
        const types = data['types'];
        const prices = data['prices'];
        setRadius(data['radius']);
        types.forEach(t => selectType(t));
        prices.forEach(p => selectPrice(p));
        // generateBoxes(selectedTypes.current, selectType, setTypes);
        // generateBoxes(priceRange.current, selectPrice, setPrices);
      })

      .catch(function (e) {
        console.log(e);
      })
  }

  const selectType = (name) => {
    selectedTypes.current[name] = !selectedTypes.current[name];
  }

  const selectPrice = (name) => {
    priceRange.current[name] = !priceRange.current[name];
  }

  useEffect(() => {
    generateObjects(TYPES, selectedTypes);
    generateObjects(PRICES, priceRange);
    generateBoxes(selectedTypes.current, selectType, setTypes);
    generateBoxes(priceRange.current, selectPrice, setPrices);
    // getPreferences();
  }, []);

  const changeRange = (e) => {
    setRadius(e.target.value);
    if (e.target.value === "1") {
      setUnit("mile");
    } else {
      setUnit("miles");
    }
  }

  const savePreferences = () => {
    // send to backend
    const typePref = [];
    const pricePref = [];
    const types = selectedTypes.current;
    const prices = priceRange.current;
    for (let type in types) {
      if (types[type]) {
        typePref.push(type);
      }
    }
    for (let price in prices) {
      if (prices[price]){
        pricePref.push(price);
      }
    }
    const toSend = {
      username: user,
      types: typePref,
      price: pricePref,
      radius: radius
    }
    console.log(toSend);
  }

  return (
    <>
      <TopBar to="/home" showOptions={false}/>
      <div className="content">
        <div className="title-text">pick your favorites</div>
        <div className="survey">
          {types}
        </div>
        <div className="title-text">pick your price range</div>
        <div className="survey">
          {prices}
        </div>
        <div className="title-text">pick your range</div>
        <div className="slider-container">
          <input id="radius" type="range" min="1" max="50" value={radius} onChange={changeRange}/>
          <div id="radius-display">
            {radius} {unit}
          </div>
        </div>
        <button className="primary-button" onClick={savePreferences}>save</button>
      </div>
      <div className="exit-home">
        <Link to="/home">
          <button className="secondary-button">
            skip
          </button>
        </Link>
      </div>
    </>
  );
}

export default Survey;
