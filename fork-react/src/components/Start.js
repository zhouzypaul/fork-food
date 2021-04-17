import React from 'react';
import {Link} from "react-router-dom";

function Start() {
  return (
    <>
      <div className="login-button">
        <Link className="links" to="/login">sign in</Link>
      </div>
      <div className="start-content">
        <svg className="bg-blob" width="800" height="446" viewBox="0 0 800 446" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M0.454898 119.161C-8.28517 194.686 110.919 365.234 185.807 434.917C239.282 470.907 237.357 412.812 368.458 352.83C499.559 292.848 727.261 339.269 771.536 330.924C806.956 324.247 805.705 218.66 787.433 174.808C778.233 152.728 741.866 112.485 678.385 119.161C599.035 127.506 538.084 26.3192 457.008 8.0638C392.148 -6.54052 283.549 1.97867 237.357 8.0638C162.031 13.6274 9.19497 43.6358 0.454898 119.161Z" fill="#DDAFF9"/>
          <path d="M0.454898 119.161C-8.28517 194.686 110.919 365.234 185.807 434.917C239.282 470.907 237.357 412.812 368.458 352.83C499.559 292.848 727.261 339.269 771.536 330.924C806.956 324.247 805.705 218.66 787.433 174.808C778.233 152.728 741.866 112.485 678.385 119.161C599.035 127.506 538.084 26.3192 457.008 8.0638C392.148 -6.54052 283.549 1.97867 237.357 8.0638C162.031 13.6274 9.19497 43.6358 0.454898 119.161Z" fill="url(#paint0_linear)"/>
          <defs>
            <linearGradient id="paint0_linear" x1="397.789" y1="0" x2="397.789" y2="448.133" gradientUnits="userSpaceOnUse">
              <stop stopColor="#FFC2F9"/>
              <stop offset="1" stopColor="white" stopOpacity="0"/>
            </linearGradient>
          </defs>
          <text className="fork-main" x="15%" y="40%" fill="black" textAnchor="left">fork</text>
          <text id="slogan" x="16%" y="50%" fill="black" textAnchor="left">
            <tspan x="16%" dy="0.8em">
              for those fork-in-the-road
            </tspan>
            <tspan x="16%" dy="1.1em">
              moments
            </tspan>
          </text>
        </svg>
        <svg className="circle-1" width="59" height="58" viewBox="0 0 59 58" fill="none" xmlns="http://www.w3.org/2000/svg">
          <ellipse rx="28.6176" ry="28.3024" transform="matrix(0.948178 -0.317738 0.325796 0.94544 29.3555 28.8513)" fill="#DDAFF9"/>
          <ellipse rx="28.6176" ry="28.3024" transform="matrix(0.948178 -0.317738 0.325796 0.94544 29.3555 28.8513)" fill="url(#paint0_linear)"/>
          <defs>
            <linearGradient id="paint0_linear" x1="28.6176" y1="0" x2="28.6176" y2="56.6049" gradientUnits="userSpaceOnUse">
              <stop stopColor="#FDB3F5"/>
              <stop offset="1" stopColor="white" stopOpacity="0"/>
            </linearGradient>
          </defs>
        </svg>
        <svg className="circle-2" width="40" height="39" viewBox="0 0 40 39" fill="none" xmlns="http://www.w3.org/2000/svg">
          <ellipse rx="18.962" ry="18.9513" transform="matrix(0.726005 0.68769 -0.697754 0.716338 19.9898 19.6155)" fill="#DDAFF9"/>
          <ellipse rx="18.962" ry="18.9513" transform="matrix(0.726005 0.68769 -0.697754 0.716338 19.9898 19.6155)" fill="url(#paint0_linear)"/>
          <defs>
            <linearGradient id="paint0_linear" x1="18.962" y1="0" x2="18.962" y2="37.9026" gradientUnits="userSpaceOnUse">
              <stop stopColor="#FDB3F5"/>
              <stop offset="1" stopColor="white" stopOpacity="0"/>
            </linearGradient>
          </defs>
        </svg>
        <svg className="circle-3" width="28" height="28" viewBox="0 0 28 28" fill="none" xmlns="http://www.w3.org/2000/svg">
          <ellipse rx="13.7275" ry="13.6595" transform="matrix(0.826934 0.5623 -0.573047 0.819523 13.8458 13.9132)" fill="#DDAFF9"/>
          <ellipse rx="13.7275" ry="13.6595" transform="matrix(0.826934 0.5623 -0.573047 0.819523 13.8458 13.9132)" fill="url(#paint0_linear)"/>
          <defs>
            <linearGradient id="paint0_linear" x1="13.7275" y1="0" x2="13.7275" y2="27.319" gradientUnits="userSpaceOnUse">
              <stop stopColor="#FDB3F5"/>
              <stop offset="1" stopColor="white" stopOpacity="0"/>
            </linearGradient>
          </defs>
        </svg>
      </div>
      <Link className="links" to="/about">
        <div className="about">
          about
        </div>
      </Link>
    </>
  );
}

export default Start;