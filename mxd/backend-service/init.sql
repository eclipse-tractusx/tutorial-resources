CREATE TABLE assets (
    id SERIAL PRIMARY KEY,
    asset VARCHAR(255), -- Adjust the data type and length accordingly
    createdDate TIMESTAMP DEFAULT NOW(),
    updatedDate TIMESTAMP DEFAULT NOW()
);


CREATE TABLE transfer (
    id SERIAL PRIMARY KEY,
    asset text, -- Adjust the data type and length accordingly
    contents text ,
    createdDate TIMESTAMP DEFAULT NOW(),
    updatedDate TIMESTAMP DEFAULT NOW()
);



INSERT INTO assets (asset) VALUES ('your_asset_value') RETURNING id;
INSERT INTO transfer  (asset,contents) VALUES ('your_asset_value','contents') RETURNING id;


select * from transfer;