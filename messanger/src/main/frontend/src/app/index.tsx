import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Grid, Paper, TextField, Typography} from "@mui/material";

const Application = () => {
    return <Paper elevation={3}>
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <Typography variant={"h3"}>Create a new Room with</Typography>
            </Grid>
            <Grid item xs={4}>
                <TextField label="user to invite" fullWidth/>
            </Grid>
            <Grid item xs={8}>
                <Button type="button">Invite</Button>
            </Grid>
        </Grid>
    </Paper>

}

ReactDOM.render(<Application/>, document.getElementById('app'));